package com.example.shoppe.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppe.models.Product
import com.example.shoppe.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProducts: StateFlow<Resource<List<Product>>> = _specialProducts

    private val _bestDeals = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDeals: StateFlow<Resource<List<Product>>> = _bestDeals

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts: StateFlow<Resource<List<Product>>> = _bestProducts

    private var pagingInfo = PagingInfo()

    init {
        fetchSpecialProducts()
        fetchBestDeals()
        fetchBestProducts()
    }

    fun fetchSpecialProducts() {
        viewModelScope.launch {
            _specialProducts.emit(Resource.Loading())
        }
        firestore.collection("Products").whereEqualTo("category", "Special Products").get()
            .addOnSuccessListener {
                val specialProductList = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Success(specialProductList))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchBestDeals() {
        viewModelScope.launch {
            _bestDeals.emit(Resource.Loading())
        }
        firestore.collection("Products").whereEqualTo("category", "Best Deals").get()
            .addOnSuccessListener {
                val bestDealsList = it.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestDeals.emit(Resource.Success(bestDealsList))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _bestDeals.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchBestProducts() {
        if (!pagingInfo.isEnd) {
            Log.d(TAG, "not end")
            viewModelScope.launch {
                _bestProducts.emit(Resource.Loading())
                Log.d(TAG, "fetch emit loading")
            }
            firestore.collection("Products").whereEqualTo("category", "Best Products")
                .limit(pagingInfo.page * 10).get()
                .addOnSuccessListener {
                    Log.d(TAG, "onSuccessFetch")
                    val bestProductsList = it.toObjects(Product::class.java)
                    pagingInfo.isEnd = bestProductsList == pagingInfo.oldBesetProductsList
                    pagingInfo.oldBesetProductsList = bestProductsList
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Success(bestProductsList))
                        Log.d(TAG, "fetch emit success")
                    }
                    pagingInfo.page++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    companion object {
        const val TAG = "MainCategoryViewModel"
    }
}

internal data class PagingInfo(
    var page: Long = 1,
    var oldBesetProductsList: List<Product> = emptyList(),
    var isEnd: Boolean = false,
)