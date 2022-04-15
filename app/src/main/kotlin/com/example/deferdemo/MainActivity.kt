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
import com.example.deferdemo.graphql.fragment.UserInfoBasic
import com.example.deferdemo.graphql.fragment.UserInfoProjects
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
                ProjectList(null)
            }
        }
        is MainViewModel.MainUiModel.LoadedFull -> {
            Column {
                BasicInfo(uiModel.fullInfo.userInfoBasic)
                Spacer(Modifier.height(16.dp))
                ProjectList(uiModel.fullInfo.userInfoProjects!!.projects)
            }
        }
    }
}

@Composable
private fun BasicInfo(basicInfo: UserInfoBasic) {
    Card(
        Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(basicInfo.firstName)
            Spacer(modifier = Modifier.height(4.dp))
            Text(basicInfo.lastName)
            basicInfo.email?.let { email ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(email)
            }
        }
    }
}

@Composable
private fun ProjectList(projectList: List<UserInfoProjects.Project>?) {
    Card(
        Modifier.fillMaxWidth()
    ) {
        if (projectList == null) {
            Box(Modifier
                .fillMaxWidth()
                .padding(16.dp), Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(Modifier.padding(16.dp)) {
                for (project in projectList) {
                    Text("・ ${project.name} (${project.numberOfStars} stars)")
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun MainLayoutLoadingPreview() {
    MainLayout(MainViewModel.MainUiModel.Loading)
}

@Preview(showBackground = true)
@Composable
private fun MainLayoutLoadedBasicPreview() {
    MainLayout(
        MainViewModel.MainUiModel.LoadedBasic(
            basicInfo = UserInfoBasic(
                id = "0",
                firstName = "John",
                lastName = "Smith",
                email = "john.smith@example.com"
            )
        )
    )
}
