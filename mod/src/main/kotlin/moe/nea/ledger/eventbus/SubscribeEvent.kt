package moe.nea.ledger.eventbus

@Retention(AnnotationRetention.RUNTIME)
@Target(
	AnnotationTarget.FUNCTION
)
annotation class SubscribeEvent(
	val priority: EventPriority = EventPriority.NORMAL
)