package com.example.deferdemo

import androidx.lifecycle.ViewModel
import com.apollographql.apollo3.ApolloClient
import com.example.deferdemo.graphql.MeQuery
import com.example.deferdemo.graphql.fragment.UserInfoBasic
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class MainViewModel : ViewModel() {
    sealed class MainUiModel {
        object Loading : MainUiModel()
        data class LoadedBasic(val basicInfo: UserInfoBasic) : MainUiModel()
        data class LoadedFull(val fullInfo: MeQuery.Me) : MainUiModel()
    }

    val uiModel: Flow<MainUiModel> = ApolloClient.Builder()
        .serverUrl("http://10.0.2.2:4000/graphql")
        .build()
        .query(MeQuery())
        .toFlow()
        .map { response ->
            val data = response.dataAssertNoErrors
            if (data.me.userInfoProjects != null) {
                MainUiModel.LoadedFull(data.me)
            } else {
                MainUiModel.LoadedBasic(data.me.userInfoBasic)
            }
        }
        .onStart { delay(1000) }
}
