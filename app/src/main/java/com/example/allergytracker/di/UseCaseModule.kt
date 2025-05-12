package com.example.allergytracker.di

import com.example.allergytracker.domain.repository.AllergyRepository
import com.example.allergytracker.domain.repository.ReactionRepository
import com.example.allergytracker.domain.repository.SymptomRepository
import com.example.allergytracker.domain.usecase.allergy.*
import com.example.allergytracker.domain.usecase.reaction.*
import com.example.allergytracker.domain.usecase.symptom.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    
    // Allergy Use Cases
    @Provides
    @Singleton
    fun provideGetAllergiesUseCase(repository: AllergyRepository): GetAllergiesUseCase =
        GetAllergiesUseCase(repository)
    
    @Provides
    @Singleton
    fun provideGetActiveAllergiesUseCase(repository: AllergyRepository): GetActiveAllergiesUseCase =
        GetActiveAllergiesUseCase(repository)
    
    @Provides
    @Singleton
    fun provideGetAllergyByIdUseCase(repository: AllergyRepository): GetAllergyByIdUseCase =
        GetAllergyByIdUseCase(repository)
    
    @Provides
    @Singleton
    fun provideAddAllergyUseCase(repository: AllergyRepository): AddAllergyUseCase =
        AddAllergyUseCase(repository)
    
    @Provides
    @Singleton
    fun provideUpdateAllergyUseCase(repository: AllergyRepository): UpdateAllergyUseCase =
        UpdateAllergyUseCase(repository)
    
    @Provides
    @Singleton
    fun provideDeleteAllergyUseCase(repository: AllergyRepository): DeleteAllergyUseCase =
        DeleteAllergyUseCase(repository)
    
    // Reaction Use Cases
    @Provides
    @Singleton
    fun provideGetReactionsUseCase(repository: ReactionRepository): GetReactionsUseCase =
        GetReactionsUseCase(repository)
    
    @Provides
    @Singleton
    fun provideGetReactionsByAllergyIdUseCase(repository: ReactionRepository): GetReactionsByAllergyIdUseCase =
        GetReactionsByAllergyIdUseCase(repository)
    
    @Provides
    @Singleton
    fun provideGetRecentReactionsUseCase(repository: ReactionRepository): GetRecentReactionsUseCase =
        GetRecentReactionsUseCase(repository)
    
    @Provides
    @Singleton
    fun provideAddReactionUseCase(repository: ReactionRepository): AddReactionUseCase =
        AddReactionUseCase(repository)
    
    @Provides
    @Singleton
    fun provideDeleteReactionUseCase(repository: ReactionRepository): DeleteReactionUseCase =
        DeleteReactionUseCase(repository)

    // Symptom Use Cases
    @Provides
    @Singleton
    fun provideGetAllSymptomsUseCase(repository: SymptomRepository): GetAllSymptomsUseCase =
        GetAllSymptomsUseCase(repository)
    
    @Provides
    @Singleton
    fun provideGetSymptomsByAllergyIdUseCase(repository: SymptomRepository): GetSymptomsByAllergyIdUseCase =
        GetSymptomsByAllergyIdUseCase(repository)
    
    @Provides
    @Singleton
    fun provideGetRecentSymptomsUseCase(repository: SymptomRepository): GetRecentSymptomsUseCase =
        GetRecentSymptomsUseCase(repository)
    
    @Provides
    @Singleton
    fun provideAddSymptomUseCase(repository: SymptomRepository): AddSymptomUseCase =
        AddSymptomUseCase(repository)
    
    @Provides
    @Singleton
    fun provideUpdateSymptomUseCase(repository: SymptomRepository): UpdateSymptomUseCase =
        UpdateSymptomUseCase(repository)
    
    @Provides
    @Singleton
    fun provideDeleteSymptomUseCase(repository: SymptomRepository): DeleteSymptomUseCase =
        DeleteSymptomUseCase(repository)
} 