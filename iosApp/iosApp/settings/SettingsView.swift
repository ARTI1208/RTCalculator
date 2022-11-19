//
// Created by Artem.Pichugin on 13.11.2022.
// Copyright (c) 2022 orgName. All rights reserved.
//

import SwiftUI
import Foundation
import shared

struct SettingsView : View {

    private let themes = [
        (key: AppTheme.system.name, "Follow System"),
//        (key: PreferenceValues.shared.VALUE_THEME_DAY_NIGHT, "Day/Night"),
        (key: AppTheme.light.name, "Light"),
        (key: AppTheme.dark.name, "Dark"),
//        (key: PreferenceValues.shared.VALUE_THEME_BLACK, "Black"),
    ]
    private let tabs = [
        (key: PreferenceValues.shared.VALUE_TAB_DEFAULT_CURRENCY, "Currency"),
        (key: PreferenceValues.shared.VALUE_TAB_DEFAULT_CALC, "Calculator"),
        (key: PreferenceValues.shared.VALUE_TAB_DEFAULT_UNIT, "Unit"),
//        (key: PreferenceValues.shared.VALUE_TAB_DEFAULT_SETTINGS, "Settings"),
        (key: PreferenceValues.shared.VALUE_TAB_DEFAULT_LAST, "Last opened"),
    ]

    var buildDate: String
    {

        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        formatter.timeStyle = .medium

        if let infoPath = Bundle.main.path(forResource: "Info.plist", ofType: nil),
           let infoAttr = try? FileManager.default.attributesOfItem(atPath: infoPath),
           let infoDate = infoAttr[FileAttributeKey(rawValue: "NSFileModificationDate")] as? NSDate
        { return formatter.string(from: infoDate as Date) }
        return formatter.string(from: Date())
    }

    let version = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as! String

    var body: some View {
        List {
            Section(header: Text("General")) {

                DropdownPreference(
                        title: "Default tab",
                        key: PreferenceKeys.shared.KEY_TAB_DEFAULT,
                        defaultSelectedKey: PreferenceDefaults.shared.DEFAULT_TAB,
                        options: tabs
                )

                DropdownPreference(
                        title: "Theme",
                        key: PreferenceKeys.shared.KEY_APP_THEME,
                        defaultSelectedKey: PreferenceDefaults.shared.DEFAULT_THEME.name,
                        options: themes
                )

                if themes.contains(where: { element in element.key == "black" }) {

                    SwitchPreference(
                            title: "Auto dark theme",
                            key: PreferenceKeys.shared.KEY_AUTO_DARK_THEME,
                            defaultChecked: PreferenceDefaults.shared.DEFAULT_DARK_THEME_IS_BLACK,
                            summaryOn: "Black",
                            summaryOff: "Dark"
                    )
                }
            }

            Section(header: Text("Currency")) {

            }

            Section(header: Text("Calculator")) {
                SwitchPreference(
                        title: "Divide by zero",
                        key: CalculatorKeys.shared.KEY_ZERO_DIVISION,
                        defaultChecked: CalculatorDefaults.shared.DEFAULT_ZERO_DIVISION == DivideByZero.infinity,
                        summaryOn: "Infinity",
                        summaryOff: "Error"
                )
            }

            Section(header: Text("Unit")) {

            }

            Section(header: Text("Info")) {
                Preference(title: "App version", summary: "\(version) (\(buildDate))")
            }
        }
    }
}
