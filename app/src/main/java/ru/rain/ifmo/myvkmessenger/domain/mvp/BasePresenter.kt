package ru.rain.ifmo.myvkmessenger.domain.mvp

abstract class BasePresenter<T : MvpView> {

    protected var viewState: T? = null

    fun attach(view: T) {
        viewState = view
        onAttach()
    }

    fun detach() {
        onDetach()
        viewState = null
    }

    abstract fun onAttach()

    abstract fun onDetach()
}