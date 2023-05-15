//
//  Collector.swift
//  iosApp
//
//  Created by Daniel Sau on 3/10/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import shared


class Collector<T>: Kotlinx_coroutines_coreFlowCollector {
    
    let callback:(T) -> Void

    init(callback: @escaping (T) -> Void) {
        self.callback = callback
    }
    
    func emit(value: Any?, completionHandler: @escaping (Error?) -> Void) {
        if let v = value as? T {
            callback(v)
        }
        
        completionHandler(nil)
    }
    
//    func emit(value: Any?, completionHandler: @escaping (KotlinUnit?, Error?) -> Void) {
//        // do whatever you what with the emitted value
//        if let v = value as? T {
//            callback(v)
//        }
//
//        completionHandler(KotlinUnit(), nil)
//    }
}
