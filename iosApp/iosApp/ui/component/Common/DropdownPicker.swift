//
//  DropdownPicker.swift
//  iosApp
//
//  Created by Daniel Sau on 6/11/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import SwiftUI

struct DropdownPicker: View {
    @Binding var input: String
    @State var showDropDown = false
    var data: [String]
    
    var body: some View {
        
        ZStack(alignment: .top) {
            HStack{
                TextField("", text: $input).textInputAutocapitalization(.never).disableAutocorrection(true).padding().onSubmit {
                    showDropDown = false
                }
                Button(action: {
                    showDropDown = !showDropDown
                }, label: {
                    Label(
                        title: {
                            Text("")
                        }, icon: {
                            Image(systemName: "plus")
                        })
                })
            }.background(RoundedRectangle(cornerRadius: 8)
                .fill(Color.init(red: 255, green: 245, blue: 158))
                .opacity(1))
            .overlay(alignment: .topLeading){
                if showDropDown {
                    ScrollView {
                        VStack {
                            ForEach(data) { option in
                                if (input.isEmpty || option.contains(input)) {
                                    Button(action: {
                                        input = option
                                        showDropDown = false
                                    }, label: {
                                        Text("\(option)");
                                    }).frame(maxWidth: .infinity, alignment: .center).padding()
                                }
                            }
                        }
                    }.frame(height: 150).background(RoundedRectangle(cornerRadius: 8)
                        .fill(Color.init(red: 255, green: 245, blue: 158))
                        .opacity(1)).offset(x: 0, y: 50)
                }
            }
        }.zIndex(1)
    }
}
