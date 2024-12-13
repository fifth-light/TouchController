pub use ::binrw;

use binrw::binrw;

#[binrw]
#[bw(big)]
#[derive(Debug, Clone)]
pub enum ProxyMessage {
    #[bw(magic(1u32))]
    Add { index: u32, position: (f32, f32) },
    #[bw(magic(2u32))]
    Remove { index: u32 },
    #[bw(magic(3u32))]
    Clear,
}
