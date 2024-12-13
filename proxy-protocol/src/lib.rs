pub use ::binrw;

use binrw::binrw;

#[binrw]
#[bw(big)]
#[derive(Debug, Clone)]
pub enum ProxyMessage {
    Add { index: u32, position: (f32, f32) },
    Clear,
    Remove { index: u32 },
}
