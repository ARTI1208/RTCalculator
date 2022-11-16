//
// Created by Artem.Pichugin on 14.11.2022.
// Copyright (c) 2022 orgName. All rights reserved.
//

import SwiftUI

struct CalculatorButton : View {

    private var text: any StringProtocol

    private let action: () -> Void

    init(text: any StringProtocol, action: @escaping () -> ()) {
        self.text = text
        self.action = action
    }

    init(text: any StringProtocol, action: @escaping (any StringProtocol) -> ()) {
        self.text = text
        self.action = { action(text) }
    }

    var body : some View {
        Button(action: action) {
            Text(text).font(Font.largeTitle)
        }
    }
}
