package pt.demanda.quiz.ui.page

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import pt.demanda.quiz.services.LocalDatabase

class QuestionListPageViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val database: LocalDatabase
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val debouncedQuery = MutableStateFlow("")

    val questions = debouncedQuery
        .flatMapLatest {
            createPager(database, it).flow
        }
        .cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            query.debounce(300)
                .collect {
                    debouncedQuery.value = it
                }
        }
    }

    fun setQuery(value: String) {
        _query.value = value
    }

    private fun createPager(
        database: LocalDatabase,
        query: String = ""
    ) = Pager(config = PagingConfig(10)) {
        if (query.isNotBlank()) {
            database.questionDao().searchPaginated(query)
        } else {
            database.questionDao().getAllPaginated()
        }
    }
}