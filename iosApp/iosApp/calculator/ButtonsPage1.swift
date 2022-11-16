//
// Created by Artem.Pichugin on 14.11.2022.
// Copyright (c) 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

struct ButtonsPage1: View {

    private let model: IosCalculatorModel
    
    init(_ model: IosCalculatorModel) {
        self.model = model
    }

    var body: some View {

        ButtonMatrix(data: [
            [
                ButtonData("C", action: model.clearInput),
                ButtonData("(", action: model.handleOpeningBracket),
                ButtonData(")", action: model.handleClosingBracket),
                ButtonData("DEL", action: model.deleteLastCharacter),
            ],
            [
                ButtonData("7", action: model.handleNumber),
                ButtonData("8", action: model.handleNumber),
                ButtonData("9", action: model.handleNumber),
                ButtonData("÷", action: model.appendBinaryOperationSign),
            ],
            [
                ButtonData("4", action: model.handleNumber),
                ButtonData("5", action: model.handleNumber),
                ButtonData("6", action: model.handleNumber),
                ButtonData("×", action: model.appendBinaryOperationSign),
            ],
            [
                ButtonData("1", action: model.handleNumber),
                ButtonData("2", action: model.handleNumber),
                ButtonData("3", action: model.handleNumber),
                ButtonData("-", action: model.appendBinaryOperationSign),
            ],
            [
                ButtonData("0", action: model.handleNumber),
                ButtonData(NSLocale.current.decimalSeparator ?? ".", action: model.handleFloatingPointSymbol),
                ButtonData("=", action: model.onResult),
                ButtonData("+", action: model.appendBinaryOperationSign),
            ],
        ])

    }
}
