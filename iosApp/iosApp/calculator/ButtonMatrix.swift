//
// Created by Artem.Pichugin on 14.11.2022.
// Copyright (c) 2022 orgName. All rights reserved.
//

import SwiftUI

struct ButtonData {
    let text: String
    let action: () -> Void

    init(_ text: String, action: @escaping () -> ()) {
        self.text = text
        self.action = action
    }

    init(_ text: String, action: @escaping (String) -> ()) {
        self.text = text
        self.action = { action(text) }
    }
}

struct ButtonMatrix : View {

    let data: Array<Array<ButtonData>>

    var body: some View {
        GeometryReader { geo in
            VStack(spacing: 0) {

                ForEach(data.indices) { rowIndex in
                    HStack(spacing: 0) {
                        ForEach(data[rowIndex], id: \.text) { buttonData in
                            CalculatorButton(text: buttonData.text, action: buttonData.action).frame(width: geo.size.width / CGFloat(data[rowIndex].count))
                        }
                    }.frame(height: geo.size.height / CGFloat(data.count))
                }

            }.frame(height: geo.size.height)
        }
    }
}
