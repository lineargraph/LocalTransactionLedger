package moe.nea.ledger.eventbus

import moe.nea.ledger.Ledger
import moe.nea.ledger.events.LedgerEvent
import moe.nea.ledger.utils.ErrorUtil
import moe.nea.ledger.utils.telemetry.CommonKeys
import moe.nea.ledger.utils.telemetry.ContextValue
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import java.lang.invoke.LambdaMetafactory
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.util.function.Consumer

class LedgerEventBus<T : LedgerEvent>(
	val type: Class<T>
) {

	val fabricEvent: Event<Consumer<T>> = EventFactory
		.createArrayBacked(Consumer::class.java) { eventHandlers ->
			{ event ->
				eventHandlers.forEach {
					it.accept(event)
				}
			}
		}

	companion object {
		fun post(event: LedgerEvent) {
			val invoker =
				getBus(event.javaClass)
					.fabricEvent
					.invoker()
			Ledger.leakDI()
				.provide<ErrorUtil>()
				.catch(
					CommonKeys.EVENT_MESSAGE to ContextValue.string("Error during event execution"),
					"event_instance" to event,
					"event_type" to ContextValue.string(javaClass.name)
				) {
					invoker.accept(event)
				}
		}

		fun subscribeAll(obj: Any) {
			val lookup = MethodHandles.lookup()
			for (method in obj.javaClass.methods) {
				val annotation = method.getAnnotation(SubscribeEvent::class.java) ?: continue
				val type = method.parameterTypes.single().asSubclass(LedgerEvent::class.java)
				val bus = getBus(type)
				method.isAccessible = true
				val callsite = LambdaMetafactory.metafactory(
					lookup,
					"accept",
					MethodType.methodType(Consumer::class.java, obj.javaClass),
					MethodType.methodType(null, type),
					lookup.unreflect(method),
					MethodType.methodType(null, type)
				)
				val consumer = callsite.target.invoke(obj) as Consumer<*>
				bus.registerUnchecked(consumer)
			}
		}

		fun <T : LedgerEvent> getBus(type: Class<T>): LedgerEventBus<T> {
			@Suppress("UNCHECKED_CAST")
			return buses.computeIfAbsent(type) { LedgerEventBus(it) } as LedgerEventBus<T>
		}

		private val buses = mutableMapOf<Class<out LedgerEvent>, LedgerEventBus<*>>()
	}

	fun register(consumer: Consumer<T>) {
		fabricEvent.register(consumer)
	}

	private fun registerUnchecked(consumer: Consumer<*>) {
		@Suppress("UNCHECKED_CAST")
		register(consumer as Consumer<T>)
	}

}