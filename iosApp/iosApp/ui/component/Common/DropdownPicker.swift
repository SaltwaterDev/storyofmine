//
//  DropdownPicker.swift
//  iosApp
//
//  Created by Daniel Sau on 6/11/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct DropdownPicker: View {
    @Binding var title: String
    var placeholder: String = ""
    var selectionList: [String]
    
    var body: some View {
        // TODO: Align Picker style with Android
        // TODO: allow text input
        Picker(selection: $title) {
            ForEach(selectionList, id: \.self) {item in
                Text(item)
            }
        } label: {
        }
    }
}
//
//struct DropdownPicker_Previews: PreviewProvider {
//    static var previews: some View {
//        DropdownPicker()
//    }
//}
