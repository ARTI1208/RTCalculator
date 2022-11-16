//
// Created by Artem.Pichugin on 16.11.2022.
// Copyright (c) 2022 ru.art2000. All rights reserved.
//

import SwiftUI

struct SwitchPreference : View {

    let title: String

    let summaryOn: String

    let summaryOff: String

    let key: String

    @State private var checked: Bool

    private let defaults: UserDefaults

    init(title: String, key: String, defaultChecked: Bool, summaryOn: String, summaryOff: String, defaults: UserDefaults) {
        self.title = title
        self.key = key
        self.summaryOn = summaryOn
        self.summaryOff = summaryOff
        self.defaults = defaults
        checked = defaults.object(forKey: key) as? Bool ?? defaultChecked
    }

    init(title: String, key: String, defaultChecked: Bool, summaryOn: String, summaryOff: String) {
        self.init(title: title, key: key, defaultChecked: defaultChecked, summaryOn: summaryOn, summaryOff: summaryOff, defaults: UserDefaults.standard)
    }

    var body : some View {
        Toggle(isOn: $checked) {
            VStack(alignment: .leading) {
                Text(NSLocalizedString("Divide by zero", comment: ""))
                Text(NSLocalizedString(checked ? summaryOn : summaryOff, comment: "")).font(.footnote)
            }
        }
                .onChange(of: checked) { newValue in
                    defaults.set(newValue, forKey: key)
                }
    }

}
