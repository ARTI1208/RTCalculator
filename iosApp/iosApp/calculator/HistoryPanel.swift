//
// Created by Artem.Pichugin on 19.11.2022.
// Copyright (c) 2022 ru.art2000. All rights reserved.
//

import SwiftUI
import shared

class HistoryState: ObservableObject {
    @Published var historyItems: [(date: DateWrapperItem, items: [HistoryWrapperItem])] = []
}

struct DateWrapperItem: Identifiable, Hashable {
    let item: Kotlinx_datetimeLocalDate
    let id = UUID()
}

struct HistoryWrapperItem: Identifiable {
    let item: HistoryValueItem
    let id: Int32
}

struct HistoryPanel: View {

    @ObservedObject private var state = HistoryState()

    private let model: IosCalculatorModel

    private func updateHistoryItems(items: [HistoryListItem]) {

        var newHistory = Array<(date: DateWrapperItem, items: [HistoryWrapperItem])>()

        var currentSub = Array<HistoryWrapperItem>()

        var date: Kotlinx_datetimeLocalDate? = nil

        items.forEach { element in
            switch element {
            case let dateItem as HistoryDateItem: do {
                if date != nil {
                    newHistory.append((date: DateWrapperItem(item: date!), items: currentSub))
                }

                date = dateItem.date
                currentSub = Array<HistoryWrapperItem>()
            }
            case let valueItem as HistoryValueItem: do {
                currentSub.append(HistoryWrapperItem(item: valueItem, id: valueItem.id))
            }
            default: do {
            }
            }
        }

        if date != nil {
            newHistory.append((date: DateWrapperItem(item: date!), items: currentSub))
        }

        state.historyItems = newHistory
    }

    init(_ model: IosCalculatorModel) {
        self.model = model
        model.watchHistory(onHistoryChanged: updateHistoryItems)
    }

    var body: some View {
        VStack {
            List {

                ForEach(Array(state.historyItems.enumerated()), id: \.offset) { index, groupped in
                    Section(header: Text(groupped.date.item.description())
                            .frame(maxWidth: .infinity, alignment: .center)) {
                        ForEach(groupped.items, id: \.id) { valueItem in
                            VStack {
                                if valueItem.item.comment != nil {
                                    Text(valueItem.item.comment!)
                                            .frame(maxWidth: .infinity, alignment: .trailing)
                                }
                                Text(valueItem.item.expression)
                                        .frame(maxWidth: .infinity, alignment: .trailing)
                                Text(valueItem.item.result)
                                        .frame(maxWidth: .infinity, alignment: .trailing)
                            }
                        }
                                .onDelete { set in
                                    set.forEach { v in
                                        model.removeHistoryItem(item: state.historyItems[index].items[v].item)
                                    }
                                }
                    }
                }
            }
            Button(action: { model.clearHistoryDatabase() }) {
                Text("Clear history")
            }
        }
    }

}
