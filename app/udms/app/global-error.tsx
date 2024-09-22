'use client'

import { Typography } from "@mui/material"
import Link from "next/link"

export default function GlobalError({
  error,
  reset,
}: {
  error: Error & { digest?: string }
  reset: () => void
}) {


  return (
    <html>
      <body>
        <h2>Something went wrong!</h2>
        <Link href="/home" className="udms-link-text-style">Try again</Link>
        {/* <Typography>{"error.message === " + error.message}</Typography>
        <Typography>{"error.name === " + error.name}</Typography>
        <Typography>{"error.stack === " + error.stack}</Typography> */}
      </body>
    </html>
  )
}