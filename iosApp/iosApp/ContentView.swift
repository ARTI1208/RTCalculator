import SwiftUI
import shared

struct ContentView: View {
	let calculations = DoubleCalculations(formatter: CalculatorFormatter.shared)

	@State private var input: String = ""
	@State private var result: String = ""

	var body: some View {

		Text(result)

		if #available(iOS 15.0, *) {
			TextField(
					"Calculator input",
					text: $input
			)
					.border(.secondary)
					.onSubmit {
						result = calculations.calculateForDisplay(expression: input)
					}
		} else {
			// Fallback on earlier versions
		}
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}