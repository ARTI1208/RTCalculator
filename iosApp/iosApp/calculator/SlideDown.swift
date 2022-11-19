//
// Created by Artem.Pichugin on 19.11.2022.
// Copyright (c) 2022 ru.art2000. All rights reserved.
//

import SwiftUI

struct SlideDown<Content: View, Handle: View> : View {
    @GestureState private var dragState = DragState.inactive
    @State var position = CardPosition.collapsed

    let direction: ExpandDirection = ExpandDirection.down
    let useAnchoring: Bool = true

    var content: () -> Content
    var handle: () -> Handle

    var body: some View {
        let drag = DragGesture()
                .updating($dragState) { drag, state, transaction in
                    state = .dragging(translation: drag.translation)
                }
                .onEnded(onDragEnded)

        return GeometryReader { geo in

            VStack {
                if (position == .collapsed) {

                } else {
                    self.content()
                }
                handle()
                        .gesture(drag)
                        .onTapGesture {
                            switch position {
                            case .collapsed: position = .expanded
                            case .expanded: position = .collapsed
                            default: do {}
                            }
                        }
            }
//                    .frame(idealHeight: self.position.rawValue, maxHeight: geo.size.height)
                    .frame(height: self.position.rawValue)
                    .background(Color.white)
                    .cornerRadius(10.0)
                    .shadow(color: Color(.sRGBLinear, white: 0, opacity: 0.13), radius: 10.0)
//                    .gesture(drag)
//                    .offset(y: self.position.rawValue + self.dragState.translation.height)
                    .animation(self.dragState.isDragging ? nil : .interpolatingSpring(stiffness: 300.0, damping: 30.0, initialVelocity: 10.0))
        }
    }

    private func checkAnchoring(position: CardPosition, moveDirection: ExpandDirection) -> CardPosition {
        return position
//        let positionAbove: CardPosition
//        let positionBelow: CardPosition
//        switch moveDirection {
//        case .up: {
//            positionAbove
//        }
//        }
//        return useAnchoring ? positionAbove
//                : (positionAbove != .anchored) ? positionAbove : .expanded
    }

    private func onDragEnded(drag: DragGesture.Value) {
        let verticalDirection = drag.predictedEndLocation.y - drag.location.y
        let cardTopEdgeLocation = self.position.rawValue + drag.translation.height
        let positionAbove: CardPosition
        let positionBelow: CardPosition
        let closestPosition: CardPosition

        if cardTopEdgeLocation <= CardPosition.anchored.rawValue {
            positionAbove = .collapsed
            positionBelow = .expanded
        } else {
            positionAbove = .collapsed
            positionBelow = .expanded
        }

        if (cardTopEdgeLocation - positionAbove.rawValue) < (positionBelow.rawValue - cardTopEdgeLocation) {
            closestPosition = useAnchoring ? positionAbove
                    : (positionAbove != .anchored) ? positionAbove : .expanded
        } else {
            closestPosition = useAnchoring ? positionBelow
                    : (positionBelow != .anchored) ? positionBelow : .collapsed
        }

        print("\(verticalDirection); \(positionAbove); \(positionBelow); \(closestPosition);")

        if verticalDirection > 0 {
            self.position = checkAnchoring(position: positionBelow, moveDirection: .down)
        } else if verticalDirection < 0 {
            self.position = checkAnchoring(position: positionAbove, moveDirection: .up)
        } else {
            self.position = closestPosition
        }
    }
}

enum ExpandDirection {
    case down
    case up
}

enum CardPosition: CGFloat {
    case expanded = 580
    case anchored = 500
    case collapsed = 40
}

enum DragState {
    case inactive
    case dragging(translation: CGSize)

    var translation: CGSize {
        switch self {
        case .inactive:
            return .zero
        case .dragging(let translation):
            return translation
        }
    }

    var isDragging: Bool {
        switch self {
        case .inactive:
            return false
        case .dragging:
            return true
        }
    }
}