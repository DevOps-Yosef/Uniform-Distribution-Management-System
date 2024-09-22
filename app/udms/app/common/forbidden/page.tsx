'use client'

import { Alert, Button } from '@mui/material'

export default function Page() {

  return (
    <div>
      <Alert variant="outlined" severity="error">
        Access is forbidden.
      </Alert>
    </div>
  )
}