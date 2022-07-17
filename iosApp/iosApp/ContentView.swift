import SwiftUI
import shared

struct ContentView: View {
    @StateObject var authSetting = AuthViewModel()
    
	var body: some View {
            TabView{
                WritingScreen().tabItem{Image(systemName: "pencil")
                    Text("Write")}
                StoriesScreen().tabItem{Image(systemName: "plus")
                    Text("Lounge")}
                ProfileScreen().tabItem{Image(systemName: "person.fill")
                    Text("Profile")}
            }.environmentObject(authSetting)
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
