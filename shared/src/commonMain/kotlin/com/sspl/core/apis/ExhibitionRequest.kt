package com.sspl.core.apis

import io.ktor.resources.Resource

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 06/01/2025.
 * se.muhammadimran@gmail.com
 */

@Resource("/exhibitionStall")
class ExhibitionRequest {
    
    @Resource("create")
    class Create(val parent: ExhibitionRequest = ExhibitionRequest())
    
    @Resource("{stall_id}")
    class GetSingle(val parent: ExhibitionRequest = ExhibitionRequest(), val stall_id: Int)
    
    @Resource("update/{stall_id}")
    class Update(val parent: ExhibitionRequest = ExhibitionRequest(), val stall_id: Int)
    
    @Resource("{exhibition_id}")
    class Delete(val parent: ExhibitionRequest = ExhibitionRequest(), val exhibition_id: Int)
}
