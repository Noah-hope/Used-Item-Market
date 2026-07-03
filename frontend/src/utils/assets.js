export function resolveAssetUrl(path) {
  if (!path) {
    return ''
  }

  if (/^https?:\/\//i.test(path) || path.startsWith('data:') || path.startsWith('blob:')) {
    return path
  }

  const normalizedBase = new URL(import.meta.env.BASE_URL || '/', window.location.origin)
  const normalizedPath = path.startsWith('/') ? path.slice(1) : path
  return new URL(normalizedPath, normalizedBase).toString()
}
