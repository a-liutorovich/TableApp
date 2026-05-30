package com.example.idttesttask.data.di

import com.example.idttesttask.data.datasource.RandomStringGenerator
import com.example.idttesttask.data.repository.TableRepositoryImpl
import com.example.idttesttask.domain.repository.ITableRepository
import org.koin.dsl.module

val dataModule = module {
    single { RandomStringGenerator() }
    single<ITableRepository> { TableRepositoryImpl(get()) }
}
