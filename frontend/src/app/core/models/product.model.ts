export interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  quantity: number;
  userId: string;
  images: ProductImage[];
  createdAt?: string;
}

export interface ProductUpsert {
  name: string;
  description: string;
  price: number;
  quantity: number;
}



export interface ProductImage {
  id?: string;
  url: string;
  file?: File;
  existing: boolean;
}
