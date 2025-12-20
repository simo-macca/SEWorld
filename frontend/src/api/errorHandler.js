// Handles errors from Axios requests
export function handleError(error) {
  if (error.response) {
    // The request was made, and the server responded with a status code that falls out of the range of 2xx
    console.error('Server responded with an error:', {
      status: error.response.status,
      data: error.response.data,
      headers: error.response.headers,
    })
  } else if (error.request) {
    // The request was made but no response was received
    console.error('No response received:', error.request)
  } else {
    // Something happened in setting up the request that triggered an Error
    console.error('Error in request setup:', error.message)
  }
}
