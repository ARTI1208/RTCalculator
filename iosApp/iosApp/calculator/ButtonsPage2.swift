//
// Created by Artem.Pichugin on 14.11.2022.
// Copyright (c) 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

class ButtonsPage2State: ObservableObject {
    @Published var angleType: String = "RAD"
}

struct ButtonsPage2: View {

    @ObservedObject var state = ButtonsPage2State()

    private let model: IosCalculatorModel

    func updateAngleType(type: AngleType) {
        state.angleType = type.next.display
    }

    init(_ model: IosCalculatorModel) {
        self.model = model
        model.watchAngleType(onTypeChanged: updateAngleType)
    }

    var body: some View {

        ButtonMatrix(data: [
            [
                ButtonData("DIV", action: { model.appendBinaryOperationSign(sign: "/") }),
                ButtonData("MOD", action: { model.appendBinaryOperationSign(sign: ":") }),
                ButtonData("√", action: model.handlePrefixUnaryOperationSign),
                ButtonData("^", action: model.appendBinaryOperationSign),
            ],
            [
                ButtonData("sin", action: model.handlePrefixUnaryOperationSign),
                ButtonData("cos", action: model.handlePrefixUnaryOperationSign),
                ButtonData("lg", action: model.handlePrefixUnaryOperationSign),
                ButtonData("ln", action: model.handlePrefixUnaryOperationSign),
            ],
            [
                ButtonData("tan", action: model.handlePrefixUnaryOperationSign),
                ButtonData("ctg", action: model.handlePrefixUnaryOperationSign),
                ButtonData("%", action: model.handlePostfixUnaryOperationSign),
                ButtonData("!", action: model.handlePostfixUnaryOperationSign),
            ],
            [
                ButtonData("M+", action: model.handleMemoryOperation),
                ButtonData("M-", action: model.handleMemoryOperation),
                ButtonData("MC", action: model.handleMemoryOperation),
                ButtonData("MR", action: model.handleMemoryOperation),
            ],
            [
                ButtonData("π", action: model.handleConstant),
                ButtonData("e", action: model.handleConstant),
                ButtonData("φ", action: model.handleConstant),
                ButtonData(state.angleType, action: { model.changeAngleType() }),
            ],
        ])
    }
}