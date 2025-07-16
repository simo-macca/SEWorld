<script setup>
import { RouterLink } from 'vue-router'
import { BImg } from 'bootstrap-vue-next'

import SearchBar from '@/components/SearchBar.vue'
import { useCollapseStore } from '@/stores/isCollapse.js'

const collapse = useCollapseStore()
</script>

<template>
  <header class="container-fluid border-bottom border-4 rounded-bottom mb-3">
    <nav class="navbar navbar-expand-lg">
      <div class="container-fluid">
        <div class="navbar-brand">SEWorld</div>
        <button
          class="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarSupportedContent"
          aria-controls="navbarSupportedContent"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarSupportedContent">
          <ul class="navbar-nav me-auto mb-2 mb-lg-0">
            <li class="nav-item">
              <RouterLink class="nav-link" to="/topic">Topic</RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink class="nav-link" to="/material">Material</RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink class="nav-link" to="/about">About</RouterLink>
            </li>
            <li v-if="collapse.isCollapse"><hr class="x-divider" /></li>
            <li v-if="collapse.isCollapse">
              <RouterLink class="nav-link" to="/profile">Profile</RouterLink>
            </li>
            <li v-if="collapse.isCollapse">
              <RouterLink class="nav-link" to="/">Log out</RouterLink>
            </li>
          </ul>
          <SearchBar
            v-if="!collapse.isCollapse && !$route.meta.hideSearchBar"
            class="position-absolute top-50 start-50 translate-middle"
          />
          <ul v-if="!collapse.isCollapse" class="navbar-nav ms-auto mb-2 mb-lg-0 text-end">
            <li class="nav-item dropdown">
              <button
                class="nav-link dropdown-toggle"
                data-bs-toggle="dropdown"
                aria-expanded="false"
              >
                <b-img
                  src="/src/assets/logo.svg"
                  rounded="circle"
                  alt="Circle image"
                  class="bg-primary"
                  width="50px"
                  height="50px"
                ></b-img>
              </button>
              <ul class="dropdown-menu" style="left: -80px">
                <li><RouterLink class="dropdown-item" to="/profile">Profile</RouterLink></li>
                <li><hr class="dropdown-divider" /></li>
                <li><RouterLink class="dropdown-item" to="/">Log out</RouterLink></li>
              </ul>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  </header>
</template>
