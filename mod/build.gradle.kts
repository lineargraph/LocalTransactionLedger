import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import proguard.gradle.ProGuardTask

plugins {
	idea
	java
	id("net.fabricmc.fabric-loom") version "1.17.21"
	id("com.github.johnrengelman.shadow") version "8.1.1"
	id("com.github.gmazzo.buildconfig")
	kotlin("jvm")
	id("ledger-repo")
}
val baseGroup = project.findProperty("baseGroup") as String
val mcVersion = project.property("minecraft_version") as String
val loaderVersion = project.property("loader_version") as String
val fabricApiVersion = project.property("fabric_api_version") as String
val mixinGroup = "$baseGroup.mixin"
val modid: String = project.property("modid") as String

// Toolchains:
java {
	toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

repositories {
	maven("https://libraries.minecraft.net/")
}
loom {
	clientOnlyMinecraftJar()
	log4jConfigs.from(file("log4j2.xml"))
}


// Minecraft configuration:
//loom {
//	forge {
//		pack200Provider.set(dev.architectury.pack200.java.Pack200Adapter())
//		mixinConfig("mixins.$modid.json")
//	}
//	log4jConfigs.from(file("log4j2.xml"))
//	runConfigs {
//		"client" {
//			isIdeConfigGenerated = true
//			property("ledger.bonusresourcemod", sourceSets.main.get().output.resourcesDir!!.absolutePath)
//			property("mixin.debug", "true")
//			programArgs("--tweakClass", "org.spongepowered.asm.launch.MixinTweaker")
//			programArgs("--tweakClass", "io.github.notenoughupdates.moulconfig.tweaker.DevelopmentResourceTweaker")
//		}
//		remove(getByName("server"))
//	}
//	mixin.useLegacyMixinAp.set(false)
//}

// TODO: Add an extra shadow configuration for optimizable jars
//val optShadowImpl: Configuration by configurations.creating {
//
//}

val shadowImpl: Configuration by configurations.creating {
	configurations.implementation.get().extendsFrom(this)
}

dependencies {
	minecraft("com.mojang:minecraft:${mcVersion}")
	implementation("net.fabricmc:fabric-loader:${loaderVersion}")
	implementation("net.fabricmc.fabric-api:fabric-api:${fabricApiVersion}")

	shadowImpl(kotlin("stdlib-jdk8"))
	implementation("org.jspecify:jspecify:1.0.0")

	shadowImpl("org.xerial:sqlite-jdbc:3.45.3.0")
	shadowImpl("org.notenoughupdates.moulconfig:modern-${mcVersion}:4.7.2")
	shadowImpl("io.azam.ulidj:ulidj:1.0.4")
	shadowImpl(project(":dependency-injection"))
	shadowImpl(project(":database:impl"))
	shadowImpl("moe.nea:libautoupdate:1.3.1") {
		exclude(module = "gson")
	}
	compileOnly("com.mojang:authlib:7.0.63")
//	runtimeOnly("me.djtheredstoner:DevAuth-forge-legacy:1.2.1")
	testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
}

// Tasks:

// Delete default shadow configuration
tasks.shadowJar {
	doFirst { error("Incorrect shadow JAR built!") }
}

tasks.downloadRepo {
	hash.set("dcf1dbc")
}

val generateItemIds = tasks.register("generateItemIds", GenerateItemIds::class) {
	repoHash.set(tasks.downloadRepo.get().hash)
	packageName.set("moe.nea.ledger.gen")
	outputDirectory.set(layout.buildDirectory.dir("generated/sources/itemIds"))
	repoFiles.set(tasks.downloadRepo.get().outputDirectory)
}
sourceSets.main {
	java.srcDir(generateItemIds)
}

tasks.withType<AbstractArchiveTask> {
	archiveBaseName.set(modid)
}

tasks.withType<Jar> {}

tasks.processResources {
	inputs.property("version", project.version)
	inputs.property("mcversion", mcVersion)
	inputs.property("modid", modid)
	inputs.property("basePackage", baseGroup)

	filesMatching(listOf("fabric.mod.json", "mixins.$modid.json")) {
		expand(inputs.properties)
	}
}


val proguardOutJar = project.layout.buildDirectory.file("badjars/stripped.jar")
val proguard = tasks.register("proguard", ProGuardTask::class) {
	dependsOn(tasks.jar)
	injars(tasks.jar.map { it.archiveFile })
	outjars(proguardOutJar)
	configuration(file("ledger-rules.pro"))
	val libJava = javaToolchains.launcherFor(java.toolchain)
		.get()
		.metadata.installationPath.file("jre/lib/rt.jar")
	libraryjars(libJava)
	libraryjars(configurations.compileClasspath)
}

val shadowJar2 = tasks.register("shadowJar2", ShadowJar::class) {
	destinationDirectory.set(layout.buildDirectory.dir("badjars"))
	archiveClassifier.set("all-dev")
	from(proguardOutJar)
	dependsOn(proguard)
	configurations = listOf(shadowImpl)
	relocate("moe.nea.libautoupdate", "moe.nea.ledger.deps.libautoupdate")
	relocate("io.github.notenoughupdates.moulconfig", "moe.nea.ledger.deps.moulconfig")
	relocate("io.azam.ulidj", "moe.nea.ledger.deps.ulid")
	mergeServiceFiles()
	exclude(
		// Signatures
		"META-INF/INDEX.LIST",
		"META-INF/*.SF",
		"META-INF/*.DSA",
		"META-INF/*.RSA",
		"module-info.class",

		"META-INF/*.kotlin_module",
		"META-INF/versions/**"
	)
}

tasks.jar {
	archiveClassifier.set("without-deps")
	destinationDirectory.set(layout.buildDirectory.dir("badjars"))
}

tasks.runClient {
	javaLauncher.set(javaToolchains.launcherFor(java.toolchain))
}

buildConfig {
	packageName("moe.nea.ledger.gen")
	buildConfigField("MODID", modid)
	buildConfigField("MC_VERSION", mcVersion)
}
