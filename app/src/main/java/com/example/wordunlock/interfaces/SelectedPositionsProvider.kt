package com.example.wordunlock.interfaces

interface SelectedPositionsProvider {
    fun getSelectedPositions(): Set<Int>
    fun setSelectedPositions(positions: Set<Int>)
}