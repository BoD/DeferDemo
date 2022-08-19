package com.example.deferdemo

import android.os.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.example.deferdemo.graphql.fragment.*
import com.example.deferdemo.ui.theme.*

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
        Modifier.fillMaxWidth(),
        elevation = 4.dp,
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
        Modifier.fillMaxWidth(),
        elevation = 4.dp,
    ) {
        if (projectList == null) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp), Alignment.Center
            ) {
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
