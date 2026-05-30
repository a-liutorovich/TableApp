package com.example.idttesttask.presentation.di

import com.example.idttesttask.presentation.screen.input.InputViewModel
import com.example.idttesttask.presentation.screen.table.TableViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::InputViewModel)
    viewModelOf(::TableViewModel)
}
