package com.example.idttesttask.domain.di

import com.example.idttesttask.domain.usecase.GenerateTableUseCase
import com.example.idttesttask.domain.usecase.GetTableStateUseCase
import com.example.idttesttask.domain.usecase.SaveTableStateUseCase
import com.example.idttesttask.domain.usecase.ToggleCellHighlightUseCase
import com.example.idttesttask.domain.usecase.UpdateCellTextUseCase
import com.example.idttesttask.domain.usecase.ValidateInputUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { ValidateInputUseCase() }
    factory { GenerateTableUseCase(get()) }
    factory { GetTableStateUseCase(get()) }
    factory { SaveTableStateUseCase(get()) }
    factory { ToggleCellHighlightUseCase() }
    factory { UpdateCellTextUseCase() }
}
