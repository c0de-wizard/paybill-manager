package com.thomaskioko.paybillmanager.domain.interactor.mpesarequest

import com.thomaskioko.paybillmanager.domain.executor.PostExecutionThread
import com.thomaskioko.paybillmanager.domain.executor.ThreadExecutor
import com.thomaskioko.paybillmanager.domain.model.MpesaPushResponse
import com.thomaskioko.paybillmanager.domain.model.mpesa.MpesaPushRequest
import com.thomaskioko.paybillmanager.domain.repository.MpesaRequestRepository
import com.thomaskioko.paybillmanager.domain.usecase.FlowableUseCase
import io.reactivex.Flowable
import javax.inject.Inject

open class GetMpesaStkPush @Inject constructor(
        private val repository: MpesaRequestRepository,
        threadExecutor: ThreadExecutor,
        postExecutionThread: PostExecutionThread
) : FlowableUseCase<MpesaPushResponse, GetMpesaStkPush.Params>(threadExecutor, postExecutionThread) {


    public override fun buildUseCaseObservable(params: Params?): Flowable<MpesaPushResponse> {
        if (params == null) throw IllegalArgumentException("Params can't be null")
        return repository.getMpesaStkPush(params.bearer, params.signaturePayload, params.mpesaPushRequest)
    }


    data class Params constructor(val bearer: String, val signaturePayload: String, val mpesaPushRequest: MpesaPushRequest) {
        companion object {
            fun forGetMpesaPushRequest(bearer: String, signaturePayload: String, mpesaPushRequest: MpesaPushRequest): Params {
                return Params(bearer, signaturePayload, mpesaPushRequest)
            }
        }
    }
}