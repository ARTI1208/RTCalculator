//
// Created by Artem.Pichugin on 16.11.2022.
// Copyright (c) 2022 ru.art2000. All rights reserved.
//

import SwiftUI

struct Preference : View {

    private let title: String

    private let summary: String

    init(title: String, summary: String) {
        self.title = title
        self.summary = summary
    }

    var body: some View {
        VStack(alignment: .leading) {
            Text(NSLocalizedString(title, comment: ""))
            Text(NSLocalizedString(summary, comment: "")).font(.footnote)
        }
    }

}
