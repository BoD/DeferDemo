package com.example.deferdemo

import androidx.lifecycle.ViewModel
import com.apollographql.apollo3.ApolloClient
import com.example.deferdemo.graphql.ProductQuery
import com.example.deferdemo.graphql.fragment.ProductInfoBasic
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class MainViewModel : ViewModel() {
    sealed class MainUiModel {
        object Loading : MainUiModel()
        data class LoadedBasic(val basicInfo: ProductInfoBasic) : MainUiModel()
        data class LoadedFull(val fullInfo: ProductQuery.Product) : MainUiModel()
    }

    val uiModel: Flow<MainUiModel> = ApolloClient.Builder()
        .serverUrl("http://10.0.2.2:4000/")
        .build()
        .query(ProductQuery())
        .toFlow()
        .map { response ->
            val data = response.dataAssertNoErrors
            if (data.product!!.productInfoInventory == null) {
                // 1st payload: no inventory info
                MainUiModel.LoadedBasic(data.product.productInfoBasic!!)
            } else {
                // 2nd payload: full
                MainUiModel.LoadedFull(data.product)
            }
        }
        .onStart { delay(1000) }
}
