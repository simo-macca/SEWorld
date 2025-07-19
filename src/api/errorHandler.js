// Handles errors from Axios requests./
export function handleError(error) {
  if (error.response) {
    // The request was made and the server responded with a status code that falls out of the range of 2xx
    console.error('Server responded with an error:', {
      status: error.response.status,
      data: error.response.data,
      headers: error.response.headers,
    })

    // Handle specific status codes
    switch (error.response.status) {
      case 400:
        console.error('Bad Request: The server could not understand the request.')
        break
      case 401:
        console.error('Unauthorized: Authentication is required and has failed.')
        break
      case 403:
        console.error('Forbidden: The server understands the request, but refuses to authorize it.')
        break
      case 404:
        console.error('Not Found: The requested resource could not be found.')
        break
      case 500:
        console.error(
          'Internal Server Error: The server encountered an error and could not complete your request.',
        )
        break
      default:
        console.error(`Unexpected error: ${error.response.status}`)
    }
  } else if (error.request) {
    // The request was made but no response was received
    console.error('No response received:', error.request)
  } else {
    // Something happened in setting up the request that triggered an Error
    console.error('Error in request setup:', error.message)
  }

  // Optionally, log the entire error object for debugging
  console.error('Full error details:', error)
}
