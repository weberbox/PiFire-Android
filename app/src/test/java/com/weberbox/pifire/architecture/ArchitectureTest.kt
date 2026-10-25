package com.weberbox.pifire.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import com.lemonappdev.konsist.api.verify.assertFalse
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

/**
 * The structural rules in CLAUDE.md, checked against the app's production code. Each exception is
 * listed where it's allowed, with why.
 */
class ArchitectureTest {
    private val scope = Konsist.scopeFromProduction(moduleName = "app")
    private val files = scope.files

    private val KoFileDeclaration.packageName: String
        get() = packagee?.name.orEmpty()

    @Test
    fun `core never imports feature`() {
        files
            .filter { it.packageName.startsWith("$ROOT.core") }
            .assertFalse { file -> file.imports.any { it.name.startsWith("$ROOT.feature.") } }
    }

    @Test
    fun `data never imports presentation`() {
        files
            .filter { it.packageName.contains(".data") }
            .assertFalse { file -> file.imports.any { it.name.contains(".presentation.") } }
    }

    @Test
    fun `presentation never uses DTOs`() {
        files
            .filter { it.packageName.contains(".presentation") || it.packageName.startsWith("$ROOT.application") }
            .assertFalse { file ->
                file.imports.any { it.name.startsWith("$ROOT.core.network.model.") || it.name.endsWith("Dto") }
            }
    }

    @Test
    fun `folders are named components, screen, model and util`() {
        files.assertFalse { file -> file.packageName.split('.').any { it in WRONG_FOLDER_NAMES } }
    }

    @Test
    fun `ViewModels are Hilt ViewModels next to their screen`() {
        scope
            .classes()
            .filter { it.name.endsWith("ViewModel") && it.name != "BaseViewModel" && it.name !in PLAIN_VIEW_MODELS }
            .assertTrue { viewModel ->
                viewModel.hasAnnotationWithName("HiltViewModel") &&
                    // MainViewModel belongs to MainActivity, in application.
                    (viewModel.resideInPackage("..presentation.screen..") || viewModel.name == "MainViewModel")
            }
    }

    @Test
    fun `ViewModels inject repos, not apis, DTOs, DataStore, Prefs or the socket`() {
        scope
            .classes()
            .filter { it.name.endsWith("ViewModel") }
            .assertFalse { viewModel ->
                viewModel.primaryConstructor?.parameters.orEmpty().any { parameter ->
                    val type = parameter.type.name
                    type.endsWith("Api") || type.endsWith("ApiImpl") || type.endsWith("Dto") ||
                        type.startsWith("DataStore") || type == "Prefs" || type == "SocketManager"
                }
            }
    }

    @Test
    fun `ViewModels don't switch dispatchers`() {
        files
            .filter { it.name.endsWith("ViewModel") && it.name !in VIEW_MODELS_WITH_DISPATCHERS }
            .assertFalse { file -> file.hasImport { it.name == "kotlinx.coroutines.Dispatchers" } }
    }

    @Test
    fun `repos are named Repo and RepoImpl, and implementations live in a repo package`() {
        scope.classesAndInterfacesAndObjects().assertFalse { it.name.endsWith("Repository") }
        scope
            .classes()
            .filter { it.name.endsWith("RepoImpl") }
            .assertTrue { impl ->
                // RemoteConfigRepoImpl lives with the rest of Remote Config in core/remoteconfig.
                (impl.resideInPackage("..repo..") || impl.resideInPackage("$ROOT.core.remoteconfig..")) &&
                    impl.hasParentWithName(impl.name.removeSuffix("Impl"))
            }
    }

    @Test
    fun `contracts live in presentation contract`() {
        scope
            .classes()
            .filter { it.name.endsWith("Contract") && it.name != "MainContract" }
            .assertTrue { it.resideInPackage("..presentation.contract..") }
    }

    @Test
    fun `use cases live in domain`() {
        scope
            .classes()
            .filter { it.name.endsWith("UseCase") }
            .assertTrue { it.resideInPackage("..domain..") }
    }

    @Test
    fun `reusable composables never take a ViewModel`() {
        scope
            .functions()
            .filter { it.resideInPackage("..presentation.components..") || it.resideInPackage("$ROOT.core.ui..") }
            // InputValidationSheet owns its sheet-local ViewModel through a defaulted parameter.
            .filter { it.name != "InputValidationSheet" }
            .assertFalse { function -> function.parameters.any { it.type.name.endsWith("ViewModel") } }
    }

    @Test
    fun `logging goes through Timber`() {
        files
            // The debug Timber tree is where log calls end up.
            .filter { it.name != "DebugLogTree" }
            .assertFalse { file ->
                file.hasImport { it.name == "android.util.Log" } || PRINTLN.containsMatchIn(file.text)
            }
    }

    @Test
    fun `no GlobalScope and no not-null assertions`() {
        files
            // Generated icon vectors are the one allowed exception for !!.
            .filter { !it.packageName.startsWith("$ROOT.core.ui.icons") }
            .assertFalse { file ->
                file.hasImport { it.name == "kotlinx.coroutines.GlobalScope" } ||
                    NOT_NULL_ASSERTION.containsMatchIn(file.text)
            }
    }

    @Test
    fun `navigation goes through navigateFrom and popBackStackFrom`() {
        files
            // NavHostUtils wraps the raw calls; MainActivity is outside any NavHost destination.
            .filter { it.name != "NavHostUtils" && it.name != "MainActivity" }
            .assertFalse { RAW_NAVIGATION.containsMatchIn(it.text) }
    }

    private companion object {
        const val ROOT = "com.weberbox.pifire"
        val WRONG_FOLDER_NAMES = setOf("component", "screens", "models", "utils")

        // Sheet-local ViewModels created with viewModel(), not Hilt; they hold no injected deps.
        val PLAIN_VIEW_MODELS = setOf("InputValidationViewModel", "PwmSheetViewModel", "SmartStartSheetViewModel")

        // Not migrated yet (BACKLOG.md "Dispatchers in ViewModels"); remove each as its repo takes over the IO.
        val VIEW_MODELS_WITH_DISPATCHERS = setOf(
            "AdminSettingsViewModel", "ConnectViewModel", "DashboardViewModel",
            "FinishViewModel", "HomeScreenViewModel", "InfoViewModel", "LandingViewModel",
            "ManualSettingsViewModel", "NotificationSettingsViewModel", "PelletSettingsViewModel",
            "PelletsViewModel", "PipModulesDetailViewModel", "ProbeSettingsViewModel",
            "PwmSettingsViewModel", "RecipeDetailsViewModel", "SafetySettingsViewModel",
            "SharedDetailsViewModel", "TimerSettingsViewModel", "UISettingsViewModel",
            "WorkSettingsViewModel",
        )
        val PRINTLN = Regex("""\bprintln\(""")
        val NOT_NULL_ASSERTION = Regex("""[\w)\]]!!""")
        val RAW_NAVIGATION = Regex("""\.(navigate|popBackStack)\(""")
    }
}
