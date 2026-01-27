import React from 'react';
import { Swiper, SwiperSlide } from 'swiper/react';
import { Autoplay, EffectFade } from 'swiper/modules';
import { Box } from '@mui/material';

import 'swiper/css';
import 'swiper/css/effect-fade';

const heroImages = [
    "https://images.unsplash.com/photo-1566073771259-6a8506099945?q=80&w=2070&auto=format&fit=crop", // Resort Pool
    "https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?q=80&w=2070&auto=format&fit=crop", // Hotel Exterior
    "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?q=80&w=2070&auto=format&fit=crop", // Beach Resort
    "https://images.unsplash.com/photo-1611892440504-42a792e24d32?q=80&w=2070&auto=format&fit=crop", // Modern Room
];

export default function Swipper() {
    return (
        <Box sx={{ width: '100%', height: '85vh', position: 'relative' }}>
            <Swiper
                modules={[EffectFade, Autoplay]}
                effect="fade"
                autoplay={{ delay: 5000, disableOnInteraction: false }}
                loop={true}
                speed={1500}
                style={{ width: '100%', height: '100%' }}
            >
                {heroImages.map((src, index) => (
                    <SwiperSlide key={index}>
                        <Box
                            component="img"
                            src={src}
                            alt={`Slide ${index}`}
                            sx={{
                                width: '100%',
                                height: '100%',
                                objectFit: 'cover',
                                filter: 'brightness(0.7)', // Darken for text readability
                            }}
                        />
                    </SwiperSlide>
                ))}
            </Swiper>
        </Box>
    );
};