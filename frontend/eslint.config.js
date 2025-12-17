import js from '@eslint/js'
import pluginVitest from '@vitest/eslint-plugin'
import skipFormatting from '@vue/eslint-config-prettier/skip-formatting'
import { defineConfig, globalIgnores } from 'eslint/config'
import configPrettier from 'eslint-config-prettier'
import pluginImport from 'eslint-plugin-import'
import pluginOxlint from 'eslint-plugin-oxlint'
import pluginPlaywright from 'eslint-plugin-playwright'
import pluginPrettier from 'eslint-plugin-prettier'
import pluginUnusedImports from 'eslint-plugin-unused-imports'
import pluginVue from 'eslint-plugin-vue'
import globals from 'globals'

// Prettier integration

export default defineConfig([
  {
    name: 'app/files-to-lint',
    files: ['**/*.{js,mjs,jsx,vue}'],
  },

  globalIgnores(['**/dist/**', '**/dist-ssr/**', '**/coverage/**']),

  {
    languageOptions: {
      globals: {
        ...globals.browser,
      },
    },
    plugins: {
      import: pluginImport,
      'unused-imports': pluginUnusedImports,
      prettier: pluginPrettier,
    },
    rules: {
      // Remove unused imports
      'unused-imports/no-unused-imports': 'error',
      'unused-imports/no-unused-vars': [
        'warn',
        {
          vars: 'all',
          args: 'after-used',
          ignoreRestSiblings: true,
        },
      ],

      // Import ordering
      'import/order': [
        'warn',
        {
          groups: [['builtin', 'external'], 'internal', ['parent', 'sibling', 'index']],
          'newlines-between': 'always',
          alphabetize: { order: 'asc', caseInsensitive: true },
        },
      ],
      'import/no-duplicates': 'error',
      'import/no-unresolved': 'off',
      'import/first': 'off',
      'import/newline-after-import': 'warn',

      'prettier/prettier': ['error', {}, { usePrettierrc: true }],
    },
    ...configPrettier,
  },

  js.configs.recommended,
  ...pluginVue.configs['flat/essential'],

  {
    ...pluginVitest.configs.recommended,
    files: ['src/**/__tests__/*'],
  },

  {
    ...pluginPlaywright.configs['flat/recommended'],
    files: ['e2e/**/*.{test,spec}.{js,ts,jsx,tsx}'],
  },

  ...pluginOxlint.configs['flat/recommended'],
  skipFormatting,
])
