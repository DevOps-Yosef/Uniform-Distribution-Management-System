'use client' // Error components must be Client Components

import { Alert, Button, Typography } from '@mui/material'
import { useEffect } from 'react'

export default function Page() {

  return (
    <div>
      <Alert variant="outlined" severity="error">
        An unexpected error has occurred. Please contact the system administrator.
      </Alert>
    </div>
  )
}