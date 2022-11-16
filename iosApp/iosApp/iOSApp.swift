import SwiftUI
import shared

@main
struct iOSApp: App {

//	init() {
//		#if DEBUG
//		var injectionBundlePath = "/Applications/InjectionIII.app/Contents/Resources"
//		#if targetEnvironment(macCatalyst)
//		injectionBundlePath = "\(injectionBundlePath)/macOSInjection.bundle"
//		#elseif os(iOS)
//		injectionBundlePath = "\(injectionBundlePath)/iOSInjection.bundle"
//		#endif
//		Bundle(path: injectionBundlePath)?.load()
//		#endif
//	}

	init() {
		DiHelperKt.doInitKoin()
	}

	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}