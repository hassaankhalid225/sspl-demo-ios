import SwiftUI
import WebKit

/// WKWebView wrapper for SwiftUI to display session URLs
struct SessionWebView: UIViewRepresentable {
    let url: URL
    
    func makeUIView(context: Context) -> WKWebView {
        let configuration = WKWebViewConfiguration()
        configuration.allowsInlineMediaPlayback = true
        configuration.mediaTypesRequiringUserActionForPlayback = []
        
        let webView = WKWebView(frame: .zero, configuration: configuration)
        webView.allowsBackForwardNavigationGestures = true
        webView.scrollView.isScrollEnabled = true
        
        return webView
    }
    
    func updateUIView(_ webView: WKWebView, context: Context) {
        let request = URLRequest(url: url)
        webView.load(request)
    }
}

/// Full screen view for displaying session in WebView
struct SessionWebViewScreen: View {
    let joinUrl: String
    @Environment(\.dismiss) private var dismiss
    
    var body: some View {
        NavigationView {
            Group {
                if let url = URL(string: joinUrl) {
                    SessionWebView(url: url)
                        .ignoresSafeArea(edges: .bottom)
                } else {
                    VStack {
                        Text("Invalid URL")
                            .foregroundColor(.red)
                        Text(joinUrl)
                            .font(.caption)
                            .foregroundColor(.gray)
                    }
                }
            }
            .navigationTitle("Session")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Close") {
                        dismiss()
                    }
                }
            }
        }
    }
}
