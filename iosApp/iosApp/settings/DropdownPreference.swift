//
// Created by Artem.Pichugin on 15.11.2022.
// Copyright (c) 2022 orgName. All rights reserved.
//

import SwiftUI

struct DropdownPreference: View {

    let title: String

    let key: String

    let options: Array<(key: String, String)>

    @State private var selectedKey: String

    private let defaults: UserDefaults

    init(title: String, key: String, defaultSelectedKey: String, options: Array<(key: String, String)>, defaults: UserDefaults) {
        self.title = title
        self.key = key
        self.options = options
        self.defaults = defaults
        selectedKey = defaults.string(forKey: key) ?? defaultSelectedKey
    }

    init(title: String, key: String, defaultSelectedKey: String, options: Array<(key: String, String)>) {
        self.init(title: title, key: key, defaultSelectedKey: defaultSelectedKey, options: options, defaults: UserDefaults.standard)
    }

    var body : some View {

        Picker(NSLocalizedString(title, comment: ""), selection: $selectedKey) {
            ForEach(options, id: \.key) { (key, name) in
                Text(NSLocalizedString(name, comment: "")).tag(key)
            }
        }.onChange(of: selectedKey) { newValue in defaults.set(newValue, forKey: key) }

    }
}
