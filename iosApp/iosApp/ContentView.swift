import SwiftUI
import shared

class ContentViewState: ObservableObject {
	@Published var selectedTheme: AppTheme

	init(_ selectedTheme: AppTheme) {
		self.selectedTheme = selectedTheme
	}
}

struct ContentView: View {

	@Environment(\.colorScheme) var systemColorScheme

	@State private var selectedTabKey: String

	private let generalPreferenceHelper = GeneralHelper().generalPreferenceHelper

	@ObservedObject var state: ContentViewState

	func updateSelectedTheme(newTheme: Any?) {
		let newThemeStr = newTheme as! AppTheme
		state.selectedTheme = newThemeStr
	}

	init() {
		_selectedTabKey = State(initialValue: generalPreferenceHelper.defaultTabToOpen)
		state = ContentViewState(generalPreferenceHelper.appTheme)

		PreferenceDelegatesKt.listen(generalPreferenceHelper.appThemeProperty, callback: updateSelectedTheme)
	}

	var body: some View {

		TabView(selection: $selectedTabKey) {
			CurrencyConverterView()
					.tabItem {
						Image(systemName: "dollarsign")
						Text(NSLocalizedString("Currency", comment: ""))
					}
					.tag(PreferenceValues.shared.VALUE_TAB_DEFAULT_CURRENCY)
			CalculatorView()
					.tabItem {
						Image(systemName: "plus")
						Text(NSLocalizedString("Calculator", comment: ""))
					}
					.tag(PreferenceValues.shared.VALUE_TAB_DEFAULT_CALC)
			UnitConverterView()
					.tabItem {
						Image(systemName: "square.grid.2x2")
						Text(NSLocalizedString("Unit", comment: ""))
					}
					.tag(PreferenceValues.shared.VALUE_TAB_DEFAULT_UNIT)
			SettingsView()
					.tabItem {
						Image(systemName: "gearshape")
						Text(NSLocalizedString("Settings", comment: ""))
					}
					.tag(PreferenceValues.shared.VALUE_TAB_DEFAULT_SETTINGS)
		}.onChange(of: selectedTabKey, perform: { newValue in generalPreferenceHelper.defaultTabToOpen = newValue})
				.environment(\.colorScheme, { switch state.selectedTheme {
			case AppTheme.light : return .light
			case AppTheme.dark : return .dark
			default: return systemColorScheme
		}}())

	}
}

class ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}

//	#if DEBUG
//	@objc class func injected() {
//		let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene
//		windowScene?.windows.first?.rootViewController =
//				UIHostingController(rootView: ContentView())
//	}
//	#endif

}