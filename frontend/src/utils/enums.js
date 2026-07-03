export const USER_STATUS_MAP = {
  ACTIVE: '正常',
  PASSWORD_RESET_REQUIRED: '需修改密码',
  DISABLED: '已停用',
}

export const GOODS_STATUS_MAP = {
  PENDING_REVIEW: '待审核',
  ACTIVE: '已上架',
  REJECTED: '已驳回',
  OFF_SHELF: '已下架',
  BANNED: '违规下架',
}

export const ORDER_STATUS_MAP = {
  PENDING_CONTACT: '待沟通',
  PENDING_PICKUP: '待自提',
  COMPLETED: '已完成',
}

export const DELIVERY_MODE_MAP = {
  SELF_PICKUP: '自提',
  CAMPUS_DELIVERY: '送货到校',
  BOTH: '自提/送货均可',
}

export const WANTED_STATUS_MAP = {
  OPEN: '进行中',
  CLOSED: '已关闭',
}

const ALL_MAPS = {
  userStatus: USER_STATUS_MAP,
  goodsStatus: GOODS_STATUS_MAP,
  orderStatus: ORDER_STATUS_MAP,
  deliveryMode: DELIVERY_MODE_MAP,
  wantedStatus: WANTED_STATUS_MAP,
}

export function enumLabel(mapType, value) {
  if (!value) return value
  const map = ALL_MAPS[mapType]
  if (!map) return value
  return map[value] || value
}
