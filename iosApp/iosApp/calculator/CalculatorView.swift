//
// Created by Artem.Pichugin on 13.11.2022.
// Copyright (c) 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

class CalculatorState: ObservableObject {
    @Published var input: String = ""
    @Published var result: String = ""
    @Published var angleType: String = "DEG"
    @Published var memory: String = "0"
}

struct CalculatorView : View {

    @ObservedObject var state = CalculatorState()

    private let model = IosCalculatorModel()

    func updateExpression(s: String?) {
        state.input = s ?? ""
    }

    func updateResult(s: String?) {
        state.result = s ?? ""
    }

    func updateMemory(s: String) {
        state.memory = s
    }

    func updateAngleType(type: AngleType) {
        state.angleType = type.name.prefix(3).uppercased()
    }

    init() {
        model.watchExpression(onExpressionChanged: updateExpression)
        model.watchResult(onResultChanged: updateResult)
        model.watchAngleType(onTypeChanged: updateAngleType)
        model.watchMemory(onMemoryChanged: updateMemory)
    }

    var body: some View {

        VStack {

            Text("\(state.angleType)\(state.memory == "0" ? "" : " | M\(state.memory)")")
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(.horizontal, 8)

            TextField("1+1", text: $state.input)
                    .disabled(true)
                    .multilineTextAlignment(.trailing)
                    .padding(.horizontal, 8)
                    .frame(maxWidth: .infinity, alignment: .trailing)
                    .font(.title)

            TextField("", text: $state.result)
                    .disabled(true)
                    .multilineTextAlignment(.trailing)
                    .padding(.horizontal, 8)
                    .font(.title)
                    .frame(maxWidth: .infinity, alignment: .trailing)

            ZStack(alignment: Alignment.top) {

                TabView {
                    ButtonsPage1(model)
                    ButtonsPage2(model)
                }
                        .tabViewStyle(PageTabViewStyle())
                .padding(EdgeInsets(top: 40, leading: 0, bottom: 0, trailing: 0))
//                        .indexViewStyle(PageIndexViewStyle(backgroundDisplayMode: .always))

                SlideDown(content: { HistoryPanel(model) }, handle: {
                    Text("History")
                            .frame(maxWidth: .infinity)
                })
            }
        }
    }

}
