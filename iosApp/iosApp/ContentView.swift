import SwiftUI
import shared

struct ContentView: View {
    @StateObject var authSetting = AuthViewModel()
    
	var body: some View {
            TabView{
                WritingScreen().tabItem{Image(systemName: "pencil.and.outline")
                    Text("Write")}
                StoriesScreen().tabItem{Image(systemName: "book")
                    Text("Stories")}
                ProfileScreen().tabItem{Image(systemName: "person")
                    Text("Profile")}
            }.environmentObject(authSetting)
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
