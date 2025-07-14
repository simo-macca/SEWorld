<script setup>
import { RouterLink, RouterView } from 'vue-router'
import { BImg } from 'bootstrap-vue-next'
import ThemeToggle from '@/components/ThemeToggleComponent.vue'
</script>

<template>
  <header class="container-fluid border-bottom border-4 rounded-bottom mb-3">
      <nav class="navbar navbar-expand-lg">
        <div class="container-fluid">
          <RouterLink class="navbar-brand" to="/">SEWorld</RouterLink>
          <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
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
            </ul>
            <ul class="navbar-nav ms-auto mb-2 mb-lg-0">
              <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                  <b-img src="/src/assets/logo.svg" rounded="circle" alt="Circle image" class="bg-primary" width="50px" height="50px"></b-img>
                </a>
                <ul class="dropdown-menu" style="left: -80px;">
                  <li><RouterLink class="dropdown-item" to="/profile">Profile</RouterLink></li>
                  <li><hr class="dropdown-divider"></li>
                  <li><RouterLink class="dropdown-item" to="/">Log out</RouterLink></li>
                </ul>
              </li>
            </ul>
          </div>
        </div>
      </nav>
  </header>

  <RouterView class="container-fluid ps-2" />
  <ThemeToggle />
</template>
