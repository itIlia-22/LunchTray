/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.lunchtray

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lunchtray.datasource.DataSource
import com.example.lunchtray.ui.*

// TODO: Screen enum
//класс для перечисления маршрутов
enum class MenuScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    Entree(title = R.string.choose_entree),
    Side(title = R.string.choose_side_dish),
    Accompaniment(title = R.string.choose_accompaniment),
    Checkout(title = R.string.order_checkout)

}

// TODO: AppBar

@Composable
fun LunchAppBar(
    currentScreen: MenuScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier

) {
    TopAppBar(
        { Text(text = stringResource(currentScreen.title)) },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )

}

@Composable
fun LunchTrayApp(modifier: Modifier = Modifier) {
    // TODO: Create Controller and initialization
    val navController: NavHostController = rememberNavController()

    // Create ViewModel
    val viewModel: OrderViewModel = viewModel()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = MenuScreen.valueOf(
        backStackEntry?.destination?.route ?: MenuScreen.Start.name
    )
    Scaffold(
        topBar = {
            // TODO: AppBar
            LunchAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() })
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()
        // TODO: Navigation host
        NavHost(
            navController = navController,
            startDestination = MenuScreen.Start.name,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(MenuScreen.Start.name) {
                StartOrderScreen(onStartOrderButtonClicked = {
                    navController.navigate(MenuScreen.Entree.name)
                }
                )
            }
            composable(MenuScreen.Entree.name) {
                EntreeMenuScreen(
                    options = DataSource.entreeMenuItems,
                    onCancelButtonClicked = {
                        navController.popBackStack(MenuScreen.Start.name, inclusive = false)
                    },
                    onNextButtonClicked = {
                        navController.navigate(MenuScreen.Side.name)

                    },
                    onSelectionChanged = { viewModel.updateEntree(it) }
                )

            }
            composable(MenuScreen.Side.name) {
                SideDishMenuScreen(
                    options = DataSource.sideDishMenuItems,
                    onCancelButtonClicked = {
                        navController.popBackStack(MenuScreen.Entree.name, inclusive = false)
                    },
                    onNextButtonClicked = {
                        navController.navigate(MenuScreen.Accompaniment.name)

                    },
                    onSelectionChanged = { viewModel.updateSideDish(it) }
                )

            }
            composable(MenuScreen.Accompaniment.name) {
                AccompanimentMenuScreen(
                    options = DataSource.accompanimentMenuItems,
                    onCancelButtonClicked = {
                        navController.popBackStack(MenuScreen.Side.name, inclusive = false)
                    },
                    onNextButtonClicked = {
                        navController.navigate(MenuScreen.Checkout.name)
                    },
                    onSelectionChanged = { viewModel.updateAccompaniment(it) }
                )
            }
            composable(MenuScreen.Checkout.name) {
                CheckoutScreen(
                    orderUiState = uiState,
                    onNextButtonClicked = {
                        cancelOrderAndNavigateToStart(
                        viewModel,
                        navController
                    ) },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(
                            viewModel,
                            navController
                        )
                    })
            }
        }
    }


}

//метод для возвращение на начальный экран
private fun cancelOrderAndNavigateToStart(
    viewModel: OrderViewModel,
    navController: NavHostController
) {
    viewModel.resetOrder()
    navController.popBackStack(MenuScreen.Start.name, inclusive = false)
}


/*
@Composable
private fun ScreenDialog(
    orderPrice: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activity = (LocalContext.current) as MainActivity

    AlertDialog(
        onDismissRequest = { /*TODO*/ },
        title = { Text(text = stringResource(id = R.string.titleDialog)) },
        text = { Text(text = stringResource(id = R.string.infoDialog, orderPrice)) },
        modifier = modifier,
        confirmButton = {
            TextButton(onClick = { onClick() }) {
                Text(text = stringResource(id = R.string.BottomDialog))

            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    activity.finish()
                }
            ) {
                Text(text = "Exit")
            }
        },


    )

}
 */