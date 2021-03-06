package com.thomaskioko.paybillmanager.presentation.viewmodel.bill

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thomaskioko.paybillmanager.domain.interactor.bills.GetBillByBillId
import com.thomaskioko.paybillmanager.domain.interactor.bills.GetBillByIds
import com.thomaskioko.paybillmanager.domain.interactor.bills.GetBills
import com.thomaskioko.paybillmanager.domain.model.Bill
import com.thomaskioko.paybillmanager.presentation.mapper.BillViewMapper
import com.thomaskioko.paybillmanager.presentation.model.BillView
import com.thomaskioko.paybillmanager.presentation.state.Resource
import com.thomaskioko.paybillmanager.presentation.state.ResourceState
import io.reactivex.subscribers.DisposableSubscriber
import javax.inject.Inject

@VisibleForTesting
open class GetBillsViewModel @Inject internal constructor(
        private val getBillByBillId: GetBillByBillId?,
        private val getBillById: GetBillByIds?,
        private val getBills: GetBills?,
        private val mapper: BillViewMapper
) : ViewModel() {

    @VisibleForTesting
    open val billsLiveData: MutableLiveData<Resource<List<BillView>>> = MutableLiveData()
    @VisibleForTesting
    open val billLiveData: MutableLiveData<Resource<BillView>> = MutableLiveData()


    override fun onCleared() {
        getBills?.dispose()
        getBillByBillId?.dispose()
        super.onCleared()
    }

    @VisibleForTesting
    open fun getBills(): LiveData<Resource<List<BillView>>> {
        return billsLiveData
    }

    @VisibleForTesting
    open fun getBill(): LiveData<Resource<BillView>> {
        return billLiveData
    }

    @VisibleForTesting
    open fun fetchBills() {
        billsLiveData.postValue(Resource(ResourceState.LOADING, null, null))
        getBills?.execute(BillsSubscriber())
    }


    fun fetchBillByBillId(billId: String) {
        billLiveData.postValue(Resource(ResourceState.LOADING, null, null))
        getBillByBillId?.execute(BillByBillIdSubscriber(), GetBillByBillId.Params.forBill(billId))
    }

    fun fetchBillById(billId: String, categoryId: String) {
        billLiveData.postValue(Resource(ResourceState.LOADING, null, null))
        getBillById?.execute(BillByBillIdSubscriber(), GetBillByIds.Params.forBillByIds(billId, categoryId))
    }


    inner class BillsSubscriber : DisposableSubscriber<List<Bill>>() {
        override fun onNext(t: List<Bill>) {
            billsLiveData.postValue(Resource(ResourceState.SUCCESS,
                    t.map { mapper.mapToView(it) }, null))
        }

        override fun onComplete() {}

        override fun onError(e: Throwable) {
            billsLiveData.postValue(Resource(ResourceState.ERROR, null, e.localizedMessage))
        }
    }

    inner class BillByBillIdSubscriber : DisposableSubscriber<Bill>() {
        override fun onNext(t: Bill) {
            billLiveData.postValue(Resource(ResourceState.SUCCESS, mapper.mapToView(t), null))
        }

        override fun onComplete() {}

        override fun onError(e: Throwable) {
            billLiveData.postValue(Resource(ResourceState.ERROR, null, e.localizedMessage))
        }
    }

}