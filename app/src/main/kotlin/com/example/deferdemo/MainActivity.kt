package com.example.deferdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.deferdemo.graphql.ProductQuery
import com.example.deferdemo.graphql.fragment.ProductInfoBasic
import com.example.deferdemo.graphql.fragment.ProductInfoInventory
import com.example.deferdemo.ui.theme.DeferDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: MainViewModel by viewModels()
        setContent {
            val uiModel by viewModel.uiModel.collectAsState(initial = MainViewModel.MainUiModel.Loading)
            MainLayout(uiModel)
        }
    }
}

@Composable
private fun MainLayout(uiModel: MainViewModel.MainUiModel) {
    DeferDemoTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text(stringResource(id = R.string.app_name)) })
            }
        ) { paddingValues ->
            Box(Modifier.padding(paddingValues)) {
                Box(Modifier.padding(16.dp)) {
                    MainContents(uiModel)
                }
            }
        }
    }
}

@Composable
private fun MainContents(uiModel: MainViewModel.MainUiModel) {
    when (uiModel) {
        MainViewModel.MainUiModel.Loading -> {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is MainViewModel.MainUiModel.LoadedBasic -> {
            Column {
                BasicInfo(uiModel.basicInfo)
                Spacer(Modifier.height(16.dp))
                InventoryInfo(null)
            }
        }
        is MainViewModel.MainUiModel.LoadedFull -> {
            Column {
                BasicInfo(uiModel.fullInfo.productInfoBasic!!)
                Spacer(Modifier.height(16.dp))
                InventoryInfo(uiModel.fullInfo.productInfoInventory!!)
            }
        }
    }
}

@Composable
private fun BasicInfo(basicInfo: ProductInfoBasic) {
    Card(
        Modifier.fillMaxWidth(),
        elevation = 4.dp,
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Id: ${basicInfo.id}")
            basicInfo.sku?.let { sku ->
                Spacer(modifier = Modifier.height(4.dp))
                Text("SKU: $sku")
            }
            basicInfo.dimensions?.size?.let { size ->
                Spacer(modifier = Modifier.height(4.dp))
                Text("Size: $size")
            }
        }
    }
}

@Composable
private fun InventoryInfo(inventoryInfo: ProductInfoInventory?) {
    Card(
        Modifier.fillMaxWidth(),
        elevation = 4.dp,
    ) {
        if (inventoryInfo == null) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(Modifier.padding(16.dp)) {
                Text("・ Estimated delivery: ${inventoryInfo.delivery?.estimatedDelivery ?: "unknown"}")
                Spacer(modifier = Modifier.height(4.dp))
                Text("・ Fastest delivery: ${inventoryInfo.delivery?.fastestDelivery ?: "unknown"}")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun MainLayoutLoadingPreview() {
    MainLayout(uiModel = MainViewModel.MainUiModel.Loading)
}

@Preview(showBackground = true)
@Composable
private fun MainLayoutLoadedBasicPreview() {
    MainLayout(
        uiModel = MainViewModel.MainUiModel.LoadedBasic(
            basicInfo = ProductInfoBasic(
                id = "42",
                sku = "SKU42",
                dimensions = ProductInfoBasic.Dimensions(size = "13x22x27")
            )
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun MainLayoutLoadedFullPreview() {
    MainLayout(
        uiModel = MainViewModel.MainUiModel.LoadedFull(
            fullInfo = ProductQuery.Product(
                __typename = "",
                productInfoBasic = ProductInfoBasic(
                    id = "42",
                    sku = "SKU42",
                    dimensions = ProductInfoBasic.Dimensions(size = "13x22x27")
                ),
                productInfoInventory = ProductInfoInventory(
                    delivery = ProductInfoInventory.Delivery(
                        estimatedDelivery = "2022-09-05",
                        fastestDelivery = "2022-09-01"
                    )
                )
            )
        )
    )
}
