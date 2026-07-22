export interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  quantity: number;
  userId: string;
  imageUrls: string[];
  createdAt?: string;
}

export interface ProductUpsert {
  name: string;
  description: string;
  price: number;
  quantity: number;
}

export interface Paginated<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface ProductImage {
  url: string;
  file?: File;
  existing: boolean;
}
