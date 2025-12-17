import { ref, computed, watch } from 'vue'

export function useSearch(items, searchFields) {
  const minChars = 0
  const searchQuery = ref('')
  const debouncedQuery = ref('')

  watch(
    searchQuery,
    (newQuery) => {
      debouncedQuery.value = newQuery
    },
    { immediate: true },
  )

  const filteredItems = computed(() => {
    if (!items.value) return []

    const query = debouncedQuery.value?.trim() || ''

    // Early return se la query è troppo corta o vuota
    if (query.length < minChars || !query) return items.value

    const searchTerm = query.toLowerCase()

    return items.value.filter((item) =>
      searchFields.some((field) => {
        const value = getNestedProperty(item, field)
        return value && String(value).toLowerCase().includes(searchTerm)
      }),
    )
  })

  const getNestedProperty = (obj, path) => {
    try {
      return path.split('.').reduce((current, key) => current?.[key], obj)
    } catch {
      return undefined
    }
  }

  const setSearchQuery = (query) => {
    searchQuery.value = query
  }

  const clearSearch = () => {
    searchQuery.value = ''
    debouncedQuery.value = ''
  }

  return {
    searchQuery,
    filteredItems,
    setSearchQuery,
    clearSearch,
  }
}
