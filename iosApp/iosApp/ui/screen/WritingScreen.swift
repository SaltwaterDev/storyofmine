//
//  SwiftUIView.swift
//  iosApp
//
//  Created by Daniel Sau on 5/6/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared
import ExytePopupView

struct WritingScreen: View {
    @StateObject private var writingViewModel = WritingViewModel()
    @EnvironmentObject private var authSetting: AuthViewModel
    @Binding var tabSelection: Int
    @Binding var postSuccessStory: String?
    
    @State var showMenu = false
    @State var menuItemList: [MenuItemView] = []
    
    var body: some View {
        
        let MainView = VStack{
            TextField("Untitled", text: $writingViewModel.title).textInputAutocapitalization(.never).disableAutocorrection(true).padding()
            TextEditor(text: $writingViewModel.content).textInputAutocapitalization(.never).disableAutocorrection(true).padding()
        }
            .disabled(writingViewModel.showPostPopup || writingViewModel.loading)
            .navigationBarTitle("Preview", displayMode: .inline)
            .navigationBarItems(leading: (
                Button(action: {
                    withAnimation {
                        self.showMenu.toggle()
                    }
                }) {
                    Image(systemName: "line.horizontal.3")
                        .imageScale(.large)
                }
            ), trailing: (Button(action: {
                withAnimation {
                    writingViewModel.showPostPopup.toggle()
                }
            }) {
                Image(systemName: "paperplane")
                    .imageScale(.large)
            }))
        
        let PostingDialog = VStack {
            DropdownPicker(title: $writingViewModel.selectedTopic, placeholder: "Select Topic", selectionList: writingViewModel.topicList)
            Toggle("Post to public", isOn: $writingViewModel.isPublished).onChange(of: writingViewModel.isPublished) { _isOn in
                writingViewModel.setSaveAllowed(saveAllowed: writingViewModel.isPublished && writingViewModel.saveAllowed)
                writingViewModel.setCommentAllowed(commentAllowed: writingViewModel.isPublished && writingViewModel.commentAllowed)
            }.padding()
            Toggle("Open comment", isOn: $writingViewModel.commentAllowed).disabled(!writingViewModel.isPublished).padding()
            Toggle("Savable", isOn: $writingViewModel.saveAllowed).disabled(!writingViewModel.isPublished).padding()
            HStack(alignment: .top, spacing: 20) {
                CustomButton(title: "Preview", callback: {
                    
                })
                CustomButton(title: "Post", callback: {
                    writingViewModel.postDraft()
                })
            }
        }
        .padding(20)
        .frame(width: UIScreen.screenWidth - 40, alignment: .center)
        .background(
            RoundedRectangle(cornerRadius: 8)
                .fill(Color(red: 0.85, green: 0.8, blue: 0.95))
                .opacity(1)
        )
        
        MenuView(showMenu: $showMenu, mainView: {
            MainView
        }, menuItems: menuItemList)
        .onAppear {
            writingViewModel.getAllDraftsTitle()
            menuItemList = writingViewModel.menuItemList.map({ title in
                MenuItemView(showMenu: $showMenu, title: title, callback:{ writingViewModel.onMenuClicked(indentifier: title) })
            }) + writingViewModel.draftList.map({ draft in
                MenuItemView(showMenu: $showMenu, title: draft.value, callback:{ writingViewModel.onMenuClicked(id: draft.key) })
            })
        }.onDisappear {
            writingViewModel.saveDraft()
        }.onChange(of: writingViewModel.draftList, perform: { value in
            let menuItems: [MenuItemView] = writingViewModel.menuItemList.map({ title in
                MenuItemView(showMenu: $showMenu, title: title, callback:{ writingViewModel.onMenuClicked(indentifier: title) })
            }), draftItems: [MenuItemView] = value.map({ draft in
                MenuItemView(showMenu: $showMenu, title: draft.value, callback:{ writingViewModel.onMenuClicked(id: draft.key) })
            })
            menuItemList =  menuItems + draftItems
        }).popup(isPresented: $writingViewModel.showPostPopup, closeOnTap: false, closeOnTapOutside: true, view: {
            PostingDialog
        })
        .popup(isPresented: $writingViewModel.postSuccess, dismissCallback: {
            tabSelection = 2
            postSuccessStory = writingViewModel.postSuccessStoryId
        }, view: {
            Text("Posted!")
                .padding(20)
                .frame(width: UIScreen.screenWidth - 40, alignment: .center)
                .background(
                    RoundedRectangle(cornerRadius: 8)
                        .fill(Color(red: 0.85, green: 0.8, blue: 0.95))
                        .opacity(1)
                )
        })
        .popup(isPresented: $writingViewModel.loading, view: {
            ProgressView()
                .padding(20)
                .frame(width: UIScreen.screenWidth - 40, alignment: .center)
                .background(
                    RoundedRectangle(cornerRadius: 8)
                        .fill(Color(red: 0.85, green: 0.8, blue: 0.95))
                        .opacity(1)
                )
        })
    }
}

//struct WritingScreen_Previews: PreviewProvider {
//    static var previews: some View {
//        WritingScreen()
//    }
//}
