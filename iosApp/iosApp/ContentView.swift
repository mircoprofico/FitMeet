import UIKit
import SwiftUI
import Shared

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Self.Context) -> UIViewController {
        MainViewControllerKt.MainViewController(
            supabaseUrl: requiredConfiguration("SUPABASE_URL"),
            publishableKey: requiredConfiguration("SUPABASE_PUBLISHABLE_KEY")
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Self.Context) {}
}

private func requiredConfiguration(_ key: String) -> String {
    guard let value = Bundle.main.object(forInfoDictionaryKey: key) as? String,
          !value.isEmpty,
          !value.hasPrefix("$(") else {
        fatalError("Missing \(key) in Info.plist")
    }
    return value
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea()
    }
}
